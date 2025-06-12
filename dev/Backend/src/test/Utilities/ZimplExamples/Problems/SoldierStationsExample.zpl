# Constants/Parameters
param MIN_HOURS_BETWEEN_SHIFTS := 8;     # Minimum hours between shifts for any soldier

# Sets Definition
set SOLDIERS := {"Ben", "Dan", "Ron", "Nir", "Niv", "Avi", "Shlomo"};  # Available soldiers
set STATIONS := {"Shin Gimel", "Fillbox"};                              # Guard stations
set TIME_SLOTS := {0, 4, 8, 12, 16, 20};                               # 4-hour time slots

# Derived Sets
set STATION_TIME_PAIRS := STATIONS * TIME_SLOTS;                        # All station-time combinations
set POSSIBLE_ASSIGNMENTS := SOLDIERS * STATION_TIME_PAIRS;              # All possible soldier assignments
set POSSIBLE_TRANSITIONS := {<s,st1,t1,st2,t2> in SOLDIERS * STATION_TIME_PAIRS * STATION_TIME_PAIRS | t1 < t2};

# Decision Variables
var assignment[POSSIBLE_ASSIGNMENTS] binary;              # 1 if soldier is assigned to shift
var transition[POSSIBLE_TRANSITIONS] binary;              # 1 if soldier moves between shifts

# Guard Count Variables
var min_shifts_per_soldier integer >= 0 <= 5 priority 2 startval 1;
var max_shifts_per_soldier integer >= 1 <= 7 priority 4 startval 2;
var min_hours_between_shifts integer >= MIN_HOURS_BETWEEN_SHIFTS <= MIN_HOURS_BETWEEN_SHIFTS+4
    priority 12 startval 12;

# Transition Constraints
# Ensures that if a transition exists, the soldier must be assigned to the first shift
subto transition_requires_assignment:
    forall <s,st1,t1,st2,t2> in POSSIBLE_TRANSITIONS | st1 != st2 or t1 != t2:
        transition[s,st1,t1,st2,t2] <= assignment[s,st1,t1];

# Ensures that transitions maintain consistency between connected shifts
subto transition_shift_consistency:
    forall <s,st1,t1,st2,t2> in POSSIBLE_TRANSITIONS | st1 != st2 or t1 != t2:
        transition[s,st1,t1,st2,t2] <= assignment[s,st2,t2];


# Ensures proper transition tracking for consecutive future shifts
subto forward_shift_transition:
    forall <s,st,t> in POSSIBLE_ASSIGNMENTS | t < max(TIME_SLOTS):
        vif assignment[s,st,t] == 1 and
            (sum<s2,st2,t2> in POSSIBLE_ASSIGNMENTS | s2==s and t2 > t:
                assignment[s2,st2,t2]) >= 1
        then
            sum <s3,st3,t3,st4,t4> in POSSIBLE_TRANSITIONS |
                s3 == s and (st==st3 and t==t3):
                transition[s3,st3,t3,st4,t4] == 1
        end;

# Ensures proper transition tracking for consecutive past shifts
subto backward_shift_transition:
    forall <s,st,t> in POSSIBLE_ASSIGNMENTS | t > min(TIME_SLOTS):
        vif assignment[s,st,t] == 1 and
            (sum<s2,st2,t2> in POSSIBLE_ASSIGNMENTS | s2==s and t2 < t:
                assignment[s2,st2,t2]) >= 1
        then
            sum <s3,st3,t3,st4,t4> in POSSIBLE_TRANSITIONS |
                s3 == s and (st==st4 and t==t4):
                transition[s3,st3,t3,st4,t4] == 1
        end;

# Prevents a soldier from being assigned to multiple stations in the same time slot
subto no_simultaneous_duties:
    forall <s,st,t> in POSSIBLE_ASSIGNMENTS:
        (sum<s2,st2,t2> in POSSIBLE_ASSIGNMENTS | s2 == s and t == t2:
            assignment[s2,st2,t2]) <= 1;

# Ensures exactly one soldier is assigned to each station during each time slot
subto station_coverage:
    forall <st,t> in STATION_TIME_PAIRS:
        (sum<s,st2,t2> in POSSIBLE_ASSIGNMENTS | st==st2 and t==t2:
            assignment[s,st2,t2]) == 1;

# Enforces minimum number of shifts that must be assigned to each soldier
subto minimum_shifts:
    forall <s> in SOLDIERS:
        (sum <s2,st,t> in POSSIBLE_ASSIGNMENTS | s==s2:
            assignment[s2,st,t]) >= min_shifts_per_soldier;

# Limits maximum number of shifts that can be assigned to each soldier
subto maximum_shifts:
    forall <s> in SOLDIERS:
        (sum <s2,st,t> in POSSIBLE_ASSIGNMENTS | s==s2:
            assignment[s2,st,t]) <= max_shifts_per_soldier;

# Enforces minimum time gap between consecutive shifts for each soldier
subto shift_spacing:
    forall <s,st1,t1,st2,t2> in POSSIBLE_TRANSITIONS:
        vif t2-t1 < min_hours_between_shifts then
            transition[s,st1,t1,st2,t2] == 0
        end;



# Objective: Minimize the difference between max and min shifts while maximizing spacing between shifts
minimize schedule_cost:
    (max_shifts_per_soldier-min_shifts_per_soldier) -
    (min_hours_between_shifts)**2 +
    sum<s,st1,t1> in POSSIBLE_ASSIGNMENTS:
        sum<st2,t2> in STATION_TIME_PAIRS | st2 != st1 or t2!=t1:
            (assignment[s,st1,t1] * assignment[s,st2,t2] * (t1-t2));