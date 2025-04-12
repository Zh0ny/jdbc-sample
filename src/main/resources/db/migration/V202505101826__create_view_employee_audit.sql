CREATE VIEW view_employee_audit AS
    SELECT  employee_id,
            name,
            old_name,
            salary,
            old_salary,
            birthDate,
            old_birthDate,
            operation
        FROM employees_audit
    ORDER BY created_at;
