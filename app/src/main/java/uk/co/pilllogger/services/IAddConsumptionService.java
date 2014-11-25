package uk.co.pilllogger.services;


import java.util.Date;


public interface IAddConsumptionService {

    Date getConsumptionDate();

    void setConsumptionDate(Date date);

    Date getReminderDate();

    void setReminderDate(Date date);

}
