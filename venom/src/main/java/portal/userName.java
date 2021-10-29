import java.sql.Time;

public class userName {
    public String name, password, serialNumber, scheduleStart, scheduleStop;
    public void setUserName(String userName){this.name = userName;}
    public void setPassword(String password){this.password = password;}
    public void setSerialNumber(String serialNumber){this.serialNumber = serialNumber;}



    public void setScheduleStart(String scheduleStart){this.scheduleStart = scheduleStart;}
    public void setScheduleStop(String scheduleStop){this.scheduleStop = scheduleStop;}

    public String getUserName() {return name;}
    public String getPassword() {return password;}
    public String getSerialNumber() {return serialNumber;}



    public String getScheduleStart(){return scheduleStart;}
    public String getScheduleStop(){return scheduleStop;}
}



