package it.istc.pst.oratio;

import org.junit.jupiter.api.Test;

import it.cnr.istc.pst.oratio.Solver;
import it.cnr.istc.pst.oratio.TimelinesExecutor;

public class SolverTests {

    @Test
    void testSolverCreation() {
        Solver slv = new Solver();
        slv.dispose();
    }

    @Test
    void testExecutorCreation() {
        Solver slv = new Solver();
        TimelinesExecutor exec = new TimelinesExecutor(slv);
        exec.dispose();
        slv.dispose();
    }
}
