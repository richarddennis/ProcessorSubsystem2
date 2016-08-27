/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processorsubsystem2;

/**
 *
 * @author mad_r
 */
import eduni.simjava.*;
import eduni.simjava.distributions.*;


//Same as ProcessorSubsystem1
class Source extends Sim_entity {

    private final Sim_port out;
    private final Sim_negexp_obj delay;

    Source(String name, double mean) {
        super(name);
        out = new Sim_port("Out");
        add_port(out);
                
        delay = new Sim_negexp_obj("Delay", mean);
        add_generator(delay);
    }

    @Override
    public void body() {
        for (int i = 0; i < 100; i++) {
            sim_schedule(out, 0.0, 0);
            sim_pause(delay.sample());
        }
    }
}
