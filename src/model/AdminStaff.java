package model;

public class AdminStaff extends Staff{
    public AdminStaff(String id, String name, int age, Gender gender, String staffId, String department) {
        super(id, name, age, gender, staffId, "Admin", department);
    }

    @Override
    public boolean canPrescribeMedication(){
        return false;
    }

    @Override
    public boolean canOnboardPatients(){
        return true;
    }

    @Override
    public boolean canOnboardStaff(){
        return true;
    }

    @Override
    public String getDisplayInfo(){
        return String.format("Admin %s (%s) - Dept: %s",
                getName(), getStaffId(), getDepartment());
    }
}
