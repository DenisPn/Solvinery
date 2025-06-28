# Worker set (strings)
set WORKERS := { "Alice", "Bob", "Charlie", "David", "Eve" };

# Shift types as tuples: <name, day_from, day_to, hour_from, hour_to>
set SHIFT_TYPES := {
    # Morning shift (weekdays)
    <"morning", 1, 5, 8, 16>,     # Mon-Fri, 8am-4pm
    # Afternoon shift (weekdays)
    <"afternoon", 1, 5, 16, 24>,  # Mon-Fri, 4pm-12am
    # Weekend day shift
    <"weekend_day", 6, 7, 8, 20>, # Sat-Sun, 8am-8pm
    # Peak hours reinforcement (weekdays)
    <"peak_hours", 1, 5, 10, 14>  # Mon-Fri, 10am-2pm
};

# Worker unavailability conditions: <worker_name, day_from, day_to, hour_from, hour_to>
# Can be empty
set WORKER_CONDITIONS := {};

# Generate all possible day-specific shifts
set DAYS := {1 to 7};

# Create individual assignments
set ASSIGNMENTS := { <w, sn, sf, st, sh1, sh2, d> in
    WORKERS * SHIFT_TYPES * DAYS };

# Decision Variables
var assign[ASSIGNMENTS] binary;  # 1 if worker is assigned to shift on day

# Helper parameter to handle empty WORKER_CONDITIONS
param has_conditions := if card(WORKER_CONDITIONS) > 0 then 1 else 0;

# Constraint: Worker can't be assigned during their unavailable times
# Only applied if WORKER_CONDITIONS is not empty
subto respect_unavailability:
    if has_conditions == 1 then
        forall <w, sn, sf, st, sh1, sh2, d> in ASSIGNMENTS:
            forall <worker, day_from, day_to, hour_from, hour_to> in WORKER_CONDITIONS with
                worker == w and d >= day_from and d <= day_to:
                assign[w, sn, sf, st, sh1, sh2, d] == 0
    end;

# Constraint: Each shift must be covered by at least one worker
subto shift_coverage:
    forall <sn, sf, st, sh1, sh2> in SHIFT_TYPES:
        forall <d> in DAYS with d >= sf and d <= st:
            sum <w> in WORKERS:
                assign[w, sn, sf, st, sh1, sh2, d] >= 1;

# Constraint: Worker can't be assigned to overlapping shifts
subto no_overlap:
    forall <w> in WORKERS:
        forall <d> in DAYS:
            forall <sn1, sf1, st1, sh11, sh12> in SHIFT_TYPES:
                forall <sn2, sf2, st2, sh21, sh22> in SHIFT_TYPES with sn1 != sn2:
                    assign[w, sn1, sf1, st1, sh11, sh12, d] +
                    assign[w, sn2, sf2, st2, sh21, sh22, d] <= 1;

# Constraint: Maximum shifts per worker per week
subto max_shifts_per_week:
    forall <w> in WORKERS:
        sum <sn, sf, st, sh1, sh2, d> in SHIFT_TYPES * DAYS:
            assign[w, sn, sf, st, sh1, sh2, d] <= 5;

# Optimization objective
minimize total_obj:
    sum <w> in WORKERS:
        (sum <sn, sf, st, sh1, sh2, d> in SHIFT_TYPES * DAYS:
            assign[w, sn, sf, st, sh1, sh2, d] * (sh2 - sh1))^2;