# WORKERS: Set of worker names
set WORKERS := {
    "Alice", "Bob", "Charlie", "David", "Eve",
    "Frank", "Grace", "Henry", "Iris", "Jack",
    "Karen", "Liam", "Maya", "Noah", "Olivia"
};

# SHIFT_TYPES: <ShiftName, FromWeekday, ToWeekday, StartHour, EndHour, WorkersRequired>
# FromWeekday/ToWeekday: 1-7 (Monday-Sunday)
# Hours: 0-24 (military time)
set SHIFT_TYPES := {
    <"morning", 1, 5, 7, 15, 3>,    # Weekday morning shift
    <"afternoon", 1, 5, 15, 23, 2>, # Weekday afternoon shift
    <"night", 1, 5, 23, 7, 1>,      # Weekday night shift
    <"weekend_day", 6, 7, 8, 20, 3>, # Weekend day shift
    <"weekend_night", 6, 7, 20, 8, 2> # Weekend night shift
};

# WORKER_CONDITIONS: <WorkerName, FromDay, ToDay, StartHour, EndHour>
# Represents times when workers cannot work (personal commitments, etc.)
set WORKER_CONDITIONS := {
    # Alice has medical appointments on Monday mornings
    <"Alice", 1, 1, 8, 12>,
    # Bob studies Tuesday and Thursday evenings
    <"Bob", 2, 2, 18, 22>,
    <"Bob", 4, 4, 18, 22>,
    # Charlie has family commitments on weekends
    <"Charlie", 6, 7, 0, 24>,
    # David cannot work night shifts for health reasons
    <"David", 1, 7, 22, 8>,
    # Eve has Wednesday afternoon classes
    <"Eve", 3, 3, 14, 18>,
    # Frank takes care of children on Monday and Wednesday mornings
    <"Frank", 1, 1, 7, 12>,
    <"Frank", 3, 3, 7, 12>,
    # Grace volunteers on Friday afternoons
    <"Grace", 5, 5, 13, 17>,
    # Henry has religious observance on Saturdays
    <"Henry", 6, 6, 0, 24>,
    # Iris has therapy sessions on Tuesday afternoons
    <"Iris", 2, 2, 14, 16>,
    # Jack coaches sports on Thursday evenings
    <"Jack", 4, 4, 16, 20>
};

# Basic parameters adjusted for realistic scheduling
param MIN_SHIFTS_PER_WORKER := 3;
param MAX_SHIFTS_PER_WEEK := 5;
param MAX_CONSECUTIVE_DAYS := 3;
param MIN_WEEKEND_WORKERS := 2;
param MAX_HOURS_PER_WEEK := 40;
param MIN_HOURS_BETWEEN_SHIFTS := 10;

# Set definitions
set DAYS := {1 to 7};
set WEEKEND_DAYS := {6,7};
set SHIFT_NAMES := proj(SHIFT_TYPES, <1>);

# VALID_SHIFT_DAYS: Pairs of <ShiftName, Day> that are valid according to shift definitions
set VALID_SHIFT_DAYS := { <sn, d> in SHIFT_NAMES * DAYS |
    card({<s, df, dt, hf, ht, wr> in SHIFT_TYPES with
        s == sn and d >= df and d <= dt}) >= 1
};

set VALID_ASSIGNMENTS := WORKERS * VALID_SHIFT_DAYS;

# Parameters for shift properties
param shift_duration[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] :=
    if ht >= hf then
        ht - hf
    else
        (24 - hf) + ht
    end;
param shift_start[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] := hf;
param shift_end[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] := ht;
param workers_required[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] := wr;

# Variables
var assign[VALID_ASSIGNMENTS] binary;
var hours_worked[WORKERS] real >= 0;
var weekend_shifts[WORKERS] real >= 0;
var max_hours real >= 0;
var min_hours real >= 0;

# Core constraints
subto shift_coverage:
    forall <sn, d> in VALID_SHIFT_DAYS:
        sum <w> in WORKERS:
            assign[w, sn, d] == sum <name, df, dt, hf, ht, wr> in SHIFT_TYPES with name == sn: wr;

subto no_overlap:
    forall <w> in WORKERS:
        forall <d> in DAYS:
            sum <sn> in SHIFT_NAMES with <w, sn, d> in VALID_ASSIGNMENTS:
                assign[w, sn, d] <= 1;

subto max_shifts_per_week_constraint:
    forall <w> in WORKERS:
        sum <sn, d> in VALID_SHIFT_DAYS:
            assign[w, sn, d] <= MAX_SHIFTS_PER_WEEK;

subto min_shifts_per_worker:
    forall <w> in WORKERS:
        sum <sn, d> in VALID_SHIFT_DAYS:
            assign[w, sn, d] >= MIN_SHIFTS_PER_WORKER;

subto max_hours_per_week:
    forall <w> in WORKERS:
        sum <sn, d> in VALID_SHIFT_DAYS:
            sum <name, df, dt, hf, ht, wr> in SHIFT_TYPES with name == sn:
                assign[w, sn, d] * shift_duration[name, df, dt, hf, ht, wr] <= MAX_HOURS_PER_WEEK;

subto weekend_coverage:
    forall <d> in WEEKEND_DAYS:
        sum <w> in WORKERS:
            sum <sn> in SHIFT_NAMES with <w, sn, d> in VALID_ASSIGNMENTS:
                assign[w, sn, d] >= MIN_WEEKEND_WORKERS;

subto max_consecutive_days:
    forall <w> in WORKERS:
        forall <d> in {1 to 5}:
            sum <k> in {0 to MAX_CONSECUTIVE_DAYS} with d + k <= 7:
                sum <sn> in SHIFT_NAMES with <w, sn, d + k> in VALID_ASSIGNMENTS:
                    assign[w, sn, d + k] <= MAX_CONSECUTIVE_DAYS;

subto min_hours_between_shifts:
    forall <w> in WORKERS:
        forall <sn1, d1> in VALID_SHIFT_DAYS:
            forall <sn2, d2> in VALID_SHIFT_DAYS with d2 == d1 + 1:
                forall <n1,df1,dt1,hf1,ht1,wr1> in SHIFT_TYPES with n1 == sn1:
                    forall <n2,df2,dt2,hf2,ht2,wr2> in SHIFT_TYPES with n2 == sn2:
                        if (hf2 + 24 - ht1 < MIN_HOURS_BETWEEN_SHIFTS) then
                            assign[w, sn1, d1] + assign[w, sn2, d2] <= 1
                        end;

subto respect_worker_conditions:
    forall <w, cfd, ctd, chf, cht> in WORKER_CONDITIONS:
        forall <sn, d> in VALID_SHIFT_DAYS with d >= cfd and d <= ctd:
            forall <n,df,dt,hf,ht,wr> in SHIFT_TYPES with n == sn:
                if (
                    # Condition spans midnight
                    (chf > cht and (hf >= chf or ht <= cht)) or
                    # Normal condition
                    (chf <= cht and hf < cht and ht > chf)
                ) then
                    assign[w, sn, d] == 0
                end;

# Auxiliary variable constraints
subto calculate_hours_worked:
    forall <w> in WORKERS:
        hours_worked[w] ==
            sum <sn, d> in VALID_SHIFT_DAYS:
                sum <name, df, dt, hf, ht, wr> in SHIFT_TYPES with name == sn:
                    assign[w, sn, d] * shift_duration[name, df, dt, hf, ht, wr];

subto calculate_weekend_shifts:
    forall <w> in WORKERS:
        weekend_shifts[w] ==
            sum <sn, d> in VALID_SHIFT_DAYS with d >= 6:
                assign[w, sn, d];

subto set_max_hours:
    forall <w> in WORKERS:
        hours_worked[w] <= max_hours;

subto set_min_hours:
    forall <w> in WORKERS:
        hours_worked[w] >= min_hours;

# Objective function
minimize total_obj:
    sum <w> in WORKERS: hours_worked[w] +
    2 * sum <w> in WORKERS: weekend_shifts[w] +
    3 * (max_hours - min_hours);