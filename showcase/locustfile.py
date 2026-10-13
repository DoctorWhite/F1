import logging
import os
import random
from urllib.parse import urljoin

from locust import HttpUser, between, events, task


LOGGER = logging.getLogger(__name__)

ACCESS_TOKEN = os.getenv("F1_ACCESS_TOKEN") or os.getenv("ACCESS_TOKEN")

SERVICE_URLS = {
    "driver": os.getenv("DRIVER_SERVICE_URL", "http://localhost:8080"),
    "component": os.getenv("COMPONENT_SERVICE_URL", "http://localhost:8081"),
    "race": os.getenv("RACE_SERVICE_URL", "http://localhost:8082"),
    "car": os.getenv("CAR_SERVICE_URL", "http://localhost:8083"),
}

METRICS_PATHS = {
    "driver": "/actuator/prometheus",
    "component": "/actuator/prometheus",
    "race": "/api/v1/actuator/prometheus",
    "car": "/api/v1/actuator/prometheus",
}

COMPONENT_TYPES = ("ENGINE", "SUSPENSION", "AERO", "GEARBOX", "ELECTRONICS")
COMMON_PAGE_SIZES = (5, 10, 20)


@events.init.add_listener
def report_configuration(environment, **_kwargs):
    del environment
    LOGGER.info("Formula One showcase scenario configured for: %s", SERVICE_URLS)
    if not ACCESS_TOKEN:
        LOGGER.warning(
            "F1_ACCESS_TOKEN is not set. Protected API calls will be reported as "
            "authorization failures. Set a bearer token with test_read scope for "
            "the full showcase scenario."
        )


class RaceWeekendCommandCenter(HttpUser):
    """Models a race-weekend command center reading live operational data."""

    host = "http://localhost"
    wait_time = between(0.5, 2.0)

    @property
    def auth_headers(self):
        if ACCESS_TOKEN:
            return {"Authorization": f"Bearer {ACCESS_TOKEN}"}
        return {}

    def service_url(self, service_name, path):
        return urljoin(SERVICE_URLS[service_name].rstrip("/") + "/", path.lstrip("/"))

    def get_service(self, service_name, path, name, *, auth=True):
        headers = self.auth_headers if auth else {}
        with self.client.get(
            self.service_url(service_name, path),
            name=name,
            headers=headers,
            catch_response=True,
        ) as response:
            if response.status_code in (200, 204):
                response.success()
                return response

            if response.status_code in (401, 403) and not ACCESS_TOKEN:
                response.failure("authorization required: set F1_ACCESS_TOKEN with test_read scope")
                return response

            response.failure(f"unexpected status {response.status_code}: {response.text[:200]}")
            return response

    @task(8)
    def chief_engineer_component_peak(self):
        """Chief engineers repeatedly inspect inventory and best parts."""
        page_size = random.choice(COMMON_PAGE_SIZES)
        component_type = random.choice(COMPONENT_TYPES)

        self.get_service(
            "component",
            f"/api/v1/components?size={page_size}",
            "component-service: list components",
        )
        self.get_service(
            "component",
            f"/api/v1/components/best?type={component_type}",
            "component-service: best component by type",
        )

    @task(3)
    def sporting_director_status_board(self):
        """Sporting directors monitor available drivers and scheduled races."""
        self.get_service(
            "driver",
            "/api/v1/drivers",
            "driver-service: list drivers",
        )
        self.get_service(
            "race",
            f"/api/v1/races?size={random.choice(COMMON_PAGE_SIZES)}",
            "race-service: list races",
        )

    @task(2)
    def garage_car_overview(self):
        """Garage crew checks assembled cars before a race weekend."""
        self.get_service(
            "car",
            "/api/v1/cars",
            "car-service: list cars",
        )

    @task(1)
    def observability_check(self):
        """Operators verify that each service still exports metrics."""
        service_name = random.choice(tuple(SERVICE_URLS))
        self.get_service(
            service_name,
            METRICS_PATHS[service_name],
            f"{service_name}-service: prometheus metrics",
            auth=False,
        )
