package com.embarcadero.interview.handson;

import java.util.Date;

public interface User {
    String getFirstName();
    String getLastName();
    String getLoginName();
    String getPassword();
    Date getDate();
    boolean isEnabled();
}
