package uk.co.pilllogger.factories;

import com.squareup.otto.Bus;

import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 21/08/2014
 * in uk.co.pilllogger.factories.
 */
public class PillFactory {
    private final Bus _bus;

    public PillFactory(Bus bus) {
        _bus = bus;
    }

    public Pill Create(){
        Pill pill = new Pill();

        _bus.register(pill);

        return pill;
    }

    public Pill Create(CharSequence name, float size){
        Pill pill = new Pill(name, size);

        _bus.register(pill);

        return pill;
    }
}
