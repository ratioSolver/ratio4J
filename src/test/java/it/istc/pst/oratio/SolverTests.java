package it.istc.pst.oratio;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.cnr.istc.pst.oratio.Item.ArithItem;
import it.cnr.istc.pst.oratio.Rational;
import it.cnr.istc.pst.oratio.Solver;
import it.cnr.istc.pst.oratio.SolverException;
import it.cnr.istc.pst.oratio.TimelinesExecutor;

public class SolverTests {

    @Test
    void testSolverCreation() {
        Solver slv = new Solver();
        slv.dispose();
    }

    @Test
    void testSimpleProblem() {
        Solver slv = new Solver();
        try {
            slv.read("real a; a >= 10.0;");
            slv.solve();
            ArithItem a = (ArithItem) slv.get("a");
            assertTrue(a.getValue().eq(new Rational(10)));
        } catch (SolverException | NoSuchFieldException e) {
            e.printStackTrace();
        }
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
