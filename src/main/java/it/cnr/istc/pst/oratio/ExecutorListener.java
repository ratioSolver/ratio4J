package it.cnr.istc.pst.oratio;

public interface ExecutorListener {

    public void tick(final Rational current_time);

    public void startingAtoms(final Atom[] atoms);

    public void startAtoms(final Atom[] atoms);

    public void endingAtoms(final Atom[] atoms);

    public void endAtoms(final Atom[] atoms);
}
