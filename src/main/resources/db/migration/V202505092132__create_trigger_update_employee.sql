DROP TRIGGER IF EXISTS tgr_employee_audit_update;

CREATE TRIGGER tgr_employee_audit_update
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO employees_audit (
        employee_id,
        name,
        old_name,
        salary,
        old_salary,
        birthDate,
        old_birthDate,
        operation
    ) VALUES (
        NEW.id,
        NEW.name,
        OLD.name,
        NEW.salary,
        OLD.salary,
        NEW.birthDate,
        OLD.birthDate,
        'U'
    );
END;
