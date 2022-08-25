package it.cnr.istc.pst.oratio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import it.cnr.istc.pst.oratio.Item.EnumItem;

public class TimelinesList extends ArrayList<Timeline<?>> {

    private static final long serialVersionUID = 1L;
    private final Map<Type, TimelineExtractor> extractors = new IdentityHashMap<>();
    private final Solver solver;

    TimelinesList(final Solver solver) {
        this.solver = solver;
    }

    void stateChanged() {
        clear();

        final Map<Item, Collection<Atom>> atoms = new IdentityHashMap<>();
        for (final Type t : solver.getTypes().values()) {
            if (getTimelineType(t) != null) {
                t.getInstances().forEach(i -> atoms.put(i, new ArrayList<>()));
                for (final Predicate p : t.getPredicates().values())
                    p.getInstances().stream().map(atm -> (Atom) atm)
                            .filter(atm -> (atm.getState() == Atom.AtomState.Active)).forEach(atm -> {
                                final Item tau = atm.getTau();
                                if (tau instanceof Item.EnumItem)
                                    for (final Item val : ((Item.EnumItem) tau).getVals())
                                        atoms.get(val).add(atm);
                                else
                                    atoms.get(tau).add(atm);
                            });
            }
        }

        final Set<Item> seen = new HashSet<>();
        final Queue<Map.Entry<String, Item>> q = new ArrayDeque<>();
        q.addAll(solver.getExprs().entrySet());
        while (!q.isEmpty()) {
            final Map.Entry<String, Item> entry = q.poll();
            if (!seen.contains(entry.getValue()) && !(entry.getValue() instanceof EnumItem)) {
                seen.add(entry.getValue());
                final Type type = getTimelineType(entry.getValue().getType());
                if (type != null)
                    add(extractors.get(type).build(entry.getValue(), atoms.get(entry.getValue())));
                q.addAll(entry.getValue().getExprs().entrySet());
            }
        }
    }

    private Type getTimelineType(final Type t) {
        try {
            final Queue<Type> q = new ArrayDeque<>();
            q.add(t);
            while (!q.isEmpty()) {
                final Type c_type = q.poll();
                for (final Type type : extractors.keySet())
                    if (type.isAssignableFrom(c_type))
                        return type;
                if (c_type.getPredicates().values().stream().anyMatch(p -> p.getSuperclasses().stream()
                        .anyMatch(sp -> sp.getName().equals("Impulse") || sp.getName().equals("Interval"))))
                    return t.getSolver().getType("Agent");
                q.addAll(c_type.getSuperclasses());
            }
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TimelineExtractor addExtractor(final Type type, final TimelineExtractor extractor) {
        return extractors.put(type, extractor);
    }
}
