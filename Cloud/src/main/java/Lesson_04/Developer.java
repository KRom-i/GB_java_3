package Lesson_04;

public class Developer {

    private String name;
    private String language;
    private int Salary;

    public Developer (String name, String language, int salary) {
        this.name = name;
        this.language = language;
        Salary = salary;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getLanguage () {
        return language;
    }

    public void setLanguage (String language) {
        this.language = language;
    }

    public int getSalary () {
        return Salary;
    }

    public void setSalary (int salary) {
        Salary = salary;
    }

    @Override
    public String toString () {
        return "Developer{" +
               "name='" + name + '\'' +
               ", language='" + language + '\'' +
               ", Salary=" + Salary +
               '}';
    }
}
