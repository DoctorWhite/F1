INSERT INTO cars (
    name,
    engine_id,
    suspension_id,
    aerodynamics_id,
    gearbox_id,
    electronics_id,
    status,
    assembled_at
) VALUES
      ('RB21 Chassis A', NULL, NULL, NULL, NULL, NULL, 'NEW', NULL),
      ('RB21 Chassis B', 'eng-v6-025', 'sus-front-12', 'aero-monaco-03', 'gbx-8spd-07', 'ecu-std-01', 'ASSEMBLED', TIMESTAMP '2026-04-20 14:30:00'),
      ('SF-26 Test Car', NULL, NULL, NULL, NULL, NULL, 'NEW', NULL);