# Class options as tuples: <class_name, day, start_hour, duration>
set CLASS_OPTIONS := {
    # Math101 - 2 hour class, available in morning and afternoon slots
    <"Math101", "SUNDAY", 8, 2>,      # Early morning
    <"Math101", "MONDAY", 10, 2>,     # Mid morning
    <"Math101", "WEDNESDAY", 14, 2>,  # Afternoon
    <"Math101", "THURSDAY", 16, 2>,   # Late afternoon

    # Physics102 - 3 hour lab class
    <"Physics102", "SUNDAY", 13, 3>,   # Afternoon lab
    <"Physics102", "TUESDAY", 9, 3>,   # Morning lab
    <"Physics102", "WEDNESDAY", 9, 3>,  # Morning lab
    <"Physics102", "FRIDAY", 14, 3>,   # Afternoon lab

    # Chemistry101 - 2 hour class + 3 hour lab
    <"Chemistry101", "MONDAY", 15, 2>,   # Lecture
    <"Chemistry101", "TUESDAY", 13, 3>,  # Lab
    <"Chemistry101", "THURSDAY", 11, 2>, # Lecture
    <"Chemistry101", "FRIDAY", 9, 3>,    # Lab

    # English201 - 1.5 hour language class
    <"English201", "SUNDAY", 8, 1.5>,    # Early morning
    <"English201", "MONDAY", 12, 1.5>,   # Noon
    <"English201", "WEDNESDAY", 16, 1.5>, # Late afternoon
    <"English201", "FRIDAY", 12, 1.5>,   # Noon

    # CompSci301 - 2 hour theory + 2 hour lab
    <"CompSci301", "SUNDAY", 11, 2>,     # Theory
    <"CompSci301", "MONDAY", 8, 2>,      # Lab
    <"CompSci301", "TUESDAY", 15, 2>,    # Theory
    <"CompSci301", "THURSDAY", 14, 2>,   # Lab

    # Biology201 - 2 hour lecture + 3 hour lab
    <"Biology201", "SUNDAY", 15, 2>,     # Lecture
    <"Biology201", "TUESDAY", 8, 3>,     # Morning lab
    <"Biology201", "WEDNESDAY", 11, 2>,  # Lecture
    <"Biology201", "THURSDAY", 9, 3>,    # Morning lab

    # Statistics102 - 1.5 hour class
    <"Statistics102", "MONDAY", 13, 1.5>, # After lunch
    <"Statistics102", "TUESDAY", 11, 1.5>,# Before lunch
    <"Statistics102", "THURSDAY", 8, 1.5>,# Early morning
    <"Statistics102", "FRIDAY", 15, 1.5>  # Afternoon
};


# Get unique days for counting study days
set DAYS := proj(CLASS_OPTIONS, <2>);

# Decision Variables
var selection[CLASS_OPTIONS] binary;  # 1 if this option is selected
var day_has_class[DAYS] binary;      # 1 if day has at least one class

# Constraint for day_has_class
subto set_day_has_class:
    forall <d> in DAYS:
        sum <c,d2,h,dur> in CLASS_OPTIONS with d2 == d:
            selection[c,d2,h,dur] >= day_has_class[d];

subto force_day_has_class:
    forall <d> in DAYS:
        sum <c,d2,h,dur> in CLASS_OPTIONS with d2 == d:
            selection[c,d2,h,dur] <= card(CLASS_OPTIONS) * day_has_class[d];

# Constraint: Exactly one option must be selected for each class name
subto one_option_per_class:
    forall <name,d,h,dur> in CLASS_OPTIONS:
        sum <c,d2,h2,dur2> in CLASS_OPTIONS with c == name:
            selection[c,d2,h2,dur2] == 1;

# Constraint: No overlapping classes
subto no_overlap:
    forall <c1,d1,h1,dur1> in CLASS_OPTIONS:
        forall <c2,d2,h2,dur2> in CLASS_OPTIONS with c1 != c2 and d1 == d2 and
            ((h1 <= h2 and h1 + dur1 > h2) or (h2 <= h1 and h2 + dur2 > h1)):
            selection[c1,d1,h1,dur1] + selection[c2,d2,h2,dur2] <= 1;

# Optimization function split into different objectives
minimize total_obj:
    # Part 1: Total class hours (minimize)
    (sum <c,d,h,dur> in CLASS_OPTIONS:
        selection[c,d,h,dur] * dur) -
    # Part 2: Class spacing within days (maximize gaps between classes)
    (sum <c1,d1,h1,dur1> in CLASS_OPTIONS:
        sum <c2,d2,h2,dur2> in CLASS_OPTIONS with d1 == d2 and h1 < h2:
            selection[c1,d1,h1,dur1] * selection[c2,d2,h2,dur2] * (h2 - (h1 + dur1))) +
    # Part 3: Number of study days (minimize)
    (20 * sum <d> in DAYS: day_has_class[d]);