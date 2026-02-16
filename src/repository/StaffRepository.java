package repository;

import model.Staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffRepository {
    private Map<String, Staff> staffMap; //This is in memory storage. Doctor, Nurse, AdminStaff can all be in thi map.

    public StaffRepository() {
        this.staffMap = new HashMap<>();
    }

    //Adding a new staff. True if added, false if ID exists.
    public boolean addStaff(Staff staff) {
        if(staffMap.containsKey(staff.getStaffId())){
            return false;
        }
        staffMap.put(staff.getStaffId(), staff);
        return true;
    }

    //Find staff by ID. Returns Staff object or null if not found.
    public Staff findById(String staffId) {
        return staffMap.get(staffId);
    }

    //Get all staff members
    public List<Staff> findAll() {
        return new ArrayList<>(staffMap.values());
    }

    //Find staff by role
    public List<Staff> findByRole(String role) {
        List<Staff> result = new ArrayList<>();

        for(Staff staff : staffMap.values()) {
            if(staff.getRole().equalsIgnoreCase(role)) {
                result.add(staff);
            }
        }
        return result;
    }

    //Find staff by department
    public List<Staff> findByDepartment(String department) {
        List<Staff> result = new ArrayList<>();
        for (Staff staff : staffMap.values()) {
            if (staff.getDepartment().equalsIgnoreCase(department)) {
                result.add(staff);
            }
        }
        return result;
    }

    //Update staff information
    public boolean updateStaff(Staff staff) {
        if(!staffMap.containsKey(staff.getStaffId())) {
            return false;
        }
        staffMap.put(staff.getStaffId(), staff);
        return true;
    }

    //Remove staff by ID
    public boolean removeStaff(String staffId) {
        return staffMap.remove(staffId) != null;
    }

    //check if staff exists
    public boolean exists(String staffId) {
        return staffMap.containsKey(staffId);
    }

    //Get total count of staff
    public int count() {
        return staffMap.size();
    }

}
