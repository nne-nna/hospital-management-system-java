package model;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender fromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }

        switch (input.trim().toUpperCase()) {
            case "MALE":
                return MALE;
            case "FEMALE":
                return FEMALE;
            default:
                throw new IllegalArgumentException("Invalid gender. Only Male or Female allowed.");
        }
    }
}
