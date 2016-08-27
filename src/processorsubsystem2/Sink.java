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

class Sink extends Sim_entity {

    private final Sim_port in, out1, out2;
    private final Sim_normal_obj delay;
    private final Sim_random_obj prob;

    //Enabling logging of stats
    private final Sim_stat stat; //New for this version

    //Called once
    Sink(String name, double mean, double var) {

        super(name);

        in = new Sim_port("In");
        out1 = new Sim_port("Out1");
        out2 = new Sim_port("Out2");

        add_port(in);
        add_port(out1);
        add_port(out2);

        delay = new Sim_normal_obj("Delay", mean, var);
        prob = new Sim_random_obj("Probability");

        add_generator(delay);
        add_generator(prob);

//      statistical support
        stat = new Sim_stat();
        
/*      All measurements will be automatically calculated at the end of the 
        simulation and the results will be automatically added to the 
        simulation's report file
*/
        stat.add_measure(Sim_stat.THROUGHPUT);
        stat.add_measure(Sim_stat.RESIDENCE_TIME);
        stat.add_measure("Thread use", Sim_stat.STATE_BASED, false);
        stat.calc_proportions("Thread use", new double[]{0, 1, 2, 3});
        set_stat(stat);

    }

    @Override
    public void body() {
//Called multiple times (100)
        while (Sim_system.running()) {

            Sim_event e = new Sim_event();
            sim_get_next(e);
            double before = Sim_system.sim_clock();

//            System.out.println("Time before " + before);
            sim_process(delay.sample());
            sim_completed(e);

            //Generator for HDD sampling - New value each time its called
            double p = prob.sample();

            /*The processor is now considered to have three "threads" any of 
            which can be used to process incoming jobs. 15% of jobs will go to 
            thread 1, 60% to thread 2, and 25% to thread three*/
            if (p < 0.15) {
                stat.update("Thread use", 1, before, Sim_system.sim_clock());
                System.out.println("Thread 1");

            } else if (p < 0.75) {
                System.out.println("Thread 2");

                stat.update("Thread use", 2, before, Sim_system.sim_clock());
            } else {
                System.out.println("Thread 3");
                stat.update("Thread use", 3, before, Sim_system.sim_clock());
            }

            //Same sampling to give HHD1 60% of load
            p = prob.sample();
            if (p < 0.60) {
                sim_schedule(out1, 0.0, 1);
            } else {
                sim_schedule(out2, 0.0, 1);
            }
        }
    }
}
