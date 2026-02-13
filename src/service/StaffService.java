package service;

import model.AdminStaff;
import model.Doctor;
import model.Nurse;
import model.Staff;
import repository.StaffRepository;
import util.IdGenerator;

import javax.print.Doc;
import java.util.List;

public class StaffService {
    private StaffRepository staffRepository;
    private AuthorizationService authService;

    public StaffService(StaffRepository staffRepository, AuthorizationService authService) {
        this.staffRepository = staffRepository;
        this.authService = authService;
    }

    //Onboard a new doctor. Only the admin can do this
    public Doctor onboardDoctor(Staff currentStaff, String name, int age, String gender, String department, String specialization) throws AuthorizationService.UnauthorizedException{
        //check authorization
        authService.requirePermission(currentStaff, "ONBOARD_STAFF");

        //Generate Ids
        String personId = "PER-" + IdGenerator.generateStaffId();
        String staffId = IdGenerator.generateStaffId();

        //Create doctor
        Doctor doctor = new Doctor(personId, name, age, gender, staffId, department, specialization);

        if(!staffRepository.addStaff(doctor)){
            throw new IllegalStateException("Failed to add doctor - dupliacte ID");
        }

        return doctor;
    }

    //Onboard a new nurse
    public Nurse onboardNurse(Staff currentStaff, String name, int age, String gender, String department, String ward) throws AuthorizationService.UnauthorizedException{
        authService.requirePermission(currentStaff, "ONBOARD_STAFF");

        String personId = "PER-" + IdGenerator.generateStaffId();
        String staffId = IdGenerator.generateStaffId();

        Nurse nurse = new Nurse(personId, name, age, gender, staffId, department, ward);

        if(!staffRepository.addStaff(nurse)){
            throw new IllegalStateException("Failed to add nurse - duplicate ID");
        }

        return nurse;
    }

    //Onboard a new admin staff
    public AdminStaff onboardAdmin(Staff currentStaff, String name, int age, String gender, String department) throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "ONBOARD_STAFF");

        String personId = "PER-" + IdGenerator.generateStaffId();
        String staffId = IdGenerator.generateStaffId();

        AdminStaff admin = new AdminStaff(personId, name, age, gender, staffId, department);

        if(!staffRepository.addStaff(admin)) {
            throw new IllegalStateException("Failed to add admin - Duplicate ID");
        }

        return admin;
    }
    //Find staff by Id
    public Staff findStaffById(String staffId) {
        return staffRepository.findById(staffId);
    }

    //Get all staff
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    //Get all doctors
    public List<Staff> getAllDoctors() {
        return staffRepository.findByRole("Doctor");
    }

    //Get all nurses
    public List<Staff> getAllNurses() {
        return staffRepository.findByRole("Nurse");
    }

    //Get staff by department
    public List<Staff> getStaffByDepartment(String department) {
        return staffRepository.findByDepartment(department);
    }

    //Get total staff count
    public int getStaffCount() {
        return staffRepository.count();
    }
}
