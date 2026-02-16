package model;

public class Nurse extends Staff {
    private String ward;

    public Nurse(String id, String name, int age, Gender gender, String staffId, String department, String ward) {
        super(id, name, age, gender, staffId, "Nurse", department);
        this.ward = ward;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @Override
    public boolean canPrescribeMedication(){
        return false;
    }

    @Override
    public boolean canOnboardPatients(){
        return false;
    }

    @Override
    public boolean canOnboardStaff(){
        return false;
    }

    @Override
    public String getDisplayInfo(){
        return String.format("Nurse %s (%s) - Ward: %s | Dept: %s",
                getName(), getStaffId(), ward, getDepartment());
    }
}
