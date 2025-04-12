DROP TRIGGER IF EXISTS tgr_employee_audit_insert;
CREATE TRIGGER tgr_employee_audit_insert AFTER INSERT ON employees
FOR EACH ROW
BEGIN
    INSERT INTO employees_audit (
        employee_id,
        name,
        salary,
        birthDate,
        operation
    ) VALUES (
        NEW.id,
        NEW.name,
        NEW.salary,
        NEW.birthDate,
        'I'
    );
END;
