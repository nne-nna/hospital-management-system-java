package model;

public abstract class Staff extends Person{
    private String staffId;
    private String role;
    private String department;

    public Staff(String id, String name, int age, String gender, String staffId, String role, String department) {
        super(id, name, age, gender);
        this.staffId = staffId;
        this.role = role;
        this.department = department;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    //Each staff type will have to define its permissions
    public abstract boolean canPrescribeMedication();
    public abstract boolean canOnboardPatients();
    public abstract boolean canOnboardStaff();
}
