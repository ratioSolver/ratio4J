package it.cnr.istc.pst.oratio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimelinesExecutor {

    final Solver solver;
    private final long native_handle;
    private final long executor_listener_native_handle = 0;
    private final Collection<ExecutorListener> executor_listeners = new ArrayList<>();

    public TimelinesExecutor(final Solver solver) {
        this(solver, new Rational(1));
    }

    public TimelinesExecutor(final Solver solver, final Rational units_per_tick) {
        this.solver = solver;
        this.native_handle = new_instance(units_per_tick.getNumerator(), units_per_tick.getDenominator());
    }

    private native long new_instance(long units_per_tick_num, long units_per_tick_den);

    public native void dispose();

    public synchronized native void adapt(String script) throws SolverException;

    public synchronized native void adapt(String[] files) throws SolverException;

    public synchronized native boolean is_executing();

    public synchronized native void start_execution();

    public synchronized native void pause_execution();

    public synchronized native boolean is_finished();

    public synchronized native void tick() throws ExecutorException;

    public void dontStartYet(Set<Atom> atoms) throws ExecutorException {
        dontStartYet(atoms.stream().collect(Collectors.toMap(Function.identity(), atm -> new Rational(1))));
    }

    public void dontStartYet(Map<Atom, Rational> atoms) throws ExecutorException {
        long[] ids = new long[atoms.size()];
        long[] nums = new long[atoms.size()];
        long[] dens = new long[atoms.size()];
        int i = 0;
        for (Entry<Atom, Rational> atm : atoms.entrySet()) {
            ids[i] = atm.getKey().getId();
            nums[i] = atm.getValue().getNumerator();
            dens[i] = atm.getValue().getDenominator();
            i++;
        }
        dont_start_yet(ids, nums, dens);
    }

    private synchronized native void dont_start_yet(long[] atoms, long[] nums, long[] dens) throws ExecutorException;

    public void dontEndYet(Set<Atom> atoms) throws ExecutorException {
        dontEndYet(atoms.stream().collect(Collectors.toMap(Function.identity(), atm -> new Rational(1))));
    }

    public void dontEndYet(Map<Atom, Rational> atoms) throws ExecutorException {
        long[] ids = new long[atoms.size()];
        long[] nums = new long[atoms.size()];
        long[] dens = new long[atoms.size()];
        int i = 0;
        for (Entry<Atom, Rational> atm : atoms.entrySet()) {
            ids[i] = atm.getKey().getId();
            nums[i] = atm.getValue().getNumerator();
            dens[i] = atm.getValue().getDenominator();
            i++;
        }
        dont_end_yet(ids, nums, dens);
    }

    private synchronized native void dont_end_yet(long[] atoms, long[] nums, long[] dens) throws ExecutorException;

    public void failure(Set<Atom> atoms) throws ExecutorException {
        long[] ids = new long[atoms.size()];
        int i = 0;
        for (Atom atm : atoms)
            ids[i++] = atm.getId();
        failure(ids);
    }

    private synchronized native void failure(long[] atoms) throws ExecutorException;

    private void fireTick(final long current_time_num, final long current_time_den) {
        final Rational current_time = new Rational(current_time_num, current_time_den);
        executor_listeners.stream().forEach(l -> l.tick(current_time));
    }

    private void fireStartingAtoms(final long[] atoms) {
        Atom[] c_atoms = new Atom[atoms.length];
        for (int i = 0; i < c_atoms.length; i++)
            c_atoms[i] = solver.atoms.get(atoms[i]);
        executor_listeners.stream().forEach(l -> l.startingAtoms(c_atoms));
    }

    private void fireStartAtoms(final long[] atoms) {
        Atom[] c_atoms = new Atom[atoms.length];
        for (int i = 0; i < c_atoms.length; i++)
            c_atoms[i] = solver.atoms.get(atoms[i]);
        executor_listeners.stream().forEach(l -> l.startAtoms(c_atoms));
    }

    private void fireEndingAtoms(final long[] atoms) {
        Atom[] c_atoms = new Atom[atoms.length];
        for (int i = 0; i < c_atoms.length; i++)
            c_atoms[i] = solver.atoms.get(atoms[i]);
        executor_listeners.stream().forEach(l -> l.endingAtoms(c_atoms));
    }

    private void fireEndAtoms(final long[] atoms) {
        Atom[] c_atoms = new Atom[atoms.length];
        for (int i = 0; i < c_atoms.length; i++)
            c_atoms[i] = solver.atoms.get(atoms[i]);
        executor_listeners.stream().forEach(l -> l.endAtoms(c_atoms));
    }

    public void addExecutorListener(final ExecutorListener l) {
        executor_listeners.add(l);
    }

    public void removeExecutorListener(final ExecutorListener l) {
        executor_listeners.remove(l);
    }
}
