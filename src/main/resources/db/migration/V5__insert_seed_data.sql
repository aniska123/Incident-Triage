-- Insert default teams
INSERT INTO teams (name, skill_tags, email) VALUES
('General Support', ARRAY['general', 'other'], 'general@support.com'),
('Payments Team', ARRAY['payment', 'billing', 'refund'], 'payments@support.com'),
('Auth Team', ARRAY['login', 'auth', 'password'], 'auth@support.com'),
('Infrastructure Team', ARRAY['server', 'crash', 'downtime'], 'infra@support.com');
