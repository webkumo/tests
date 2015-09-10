package net.webcumo.tests.entities;

import com.embarcadero.interview.handson.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="User")
@TypeDefs(value = { @TypeDef(name = "password", typeClass = PasswordType.class) })
public class UserEntity implements User {
    @Column(name="firstName")
    @Getter @Setter
    private String firstName;

    @Column(name="lastName")
    @Getter @Setter
    private String lastName;

    @Id
    @Column(name="login")
    @Getter @Setter
    private String loginName;

    @Type(type = "password")
    @Column(name="password")
    @JsonIgnore
    @Getter @Setter
    private String password;

    @Column(name="date")
    private Date date;

    @Column(name="enabled")
    @Getter @Setter
    private boolean enabled;

    @Override
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setDate(Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", date=" + date +
                ", enabled=" + enabled +
                '}';
    }
}
