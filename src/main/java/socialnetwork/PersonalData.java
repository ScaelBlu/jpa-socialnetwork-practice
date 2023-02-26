package socialnetwork;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class PersonalData {

    @Column(table = "personal_data", name = "real_name")
    private String realName;

    @Column(table = "personal_data", name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(table = "personal_data")
    private String city;

    public PersonalData() {
    }

    public PersonalData(String realName, LocalDate dateOfBirth, String city) {
        this.realName = realName;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
