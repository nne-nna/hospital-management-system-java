package model;

//The person class is abstract cause we never create a generic person only specific types(staff and patient).
public abstract class Person {
    private String Id;
    private String name;
    private int age;
    private Gender gender;

    public Person(String id, String name, int age, Gender gender) {
        Id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    //This abstract method will force the subclasses to define the method and display information.
    public abstract String getDisplayInfo();

}
