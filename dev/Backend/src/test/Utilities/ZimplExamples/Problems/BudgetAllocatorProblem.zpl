set MONTHS := {1..12};
param MONTHLY_INCOME := 2500;
param MINIMUM_SAVINGS := 100;
param DESIRED_SAVINGS := 500;

# One-time events sets with explanation
set EXTRA_EARNINGS := {
    <3, 800, "Tax return">,
    <12, 400, "Holiday bonus">,
    <7, 200, "Side gig payment">
};

set EXTRA_EXPENSES := {
    <12, 300, "Holiday gifts">,
    <7, 400, "Car insurance deductible">,
    <9, 250, "Annual health checkup copay">,
    <4, 150, "Birthday gifts">,
    <1, 200, "Winter heating bill spike">
};

# Category definition with priority weights
set Bills := {
    # <category, min, max, priority_weight>
    <"Rent", 1400, 1400, 10>,        # Fixed cost - weight doesn't matter
    <"Utilities", 120, 250, 8>,       # High priority variable cost
    <"Groceries", 300, 450, 9>,       # Essential variable cost
    <"Transport", 150, 150, 10>,      # Fixed cost - weight doesn't matter
    <"Entertainment", 50, 200, 3>      # Lower priority variable cost
};

set CATEGORIES := proj(Bills, <1>);

var spending[CATEGORIES * MONTHS] integer >= 0;
var savings[MONTHS] integer >= 0;
var savings_deficit[MONTHS] real >= 0;

# Monthly budget must be fully allocated
subto monthly_budget:
    forall <m> in MONTHS:
        sum<c> in CATEGORIES:
            spending[c,m] + savings[m] ==
            MONTHLY_INCOME +
            sum<month, amount, reason> in EXTRA_EARNINGS with month == m:
                amount;

# Category spending constraints
subto category_Bills:
    forall <c> in CATEGORIES:
        forall <m> in MONTHS:
            forall <cat, minval, maxval, weight> in Bills with cat == c:
                spending[c,m] <= maxval;

# Basic minimum spending
subto minimum_spending:
    forall <c> in CATEGORIES:
        forall <m> in MONTHS:
            forall <cat, minval, maxval, weight> in Bills with cat == c:
                spending[c,m] >= minval;

# For zero-weight categories, force minimum spending
subto zero_weight_categories:
    forall <c> in CATEGORIES:
        forall <m> in MONTHS:
            forall <cat, minval, maxval, weight> in Bills with cat == c and weight == 0:
                spending[c,m] == minval;

# Minimum savings with extra expenses
subto minimum_savings:
    forall <m> in MONTHS:
        savings[m] >= MINIMUM_SAVINGS +
        sum<month, amount, reason> in EXTRA_EXPENSES with month == m:
            amount;

# Calculate savings deficit relative to desired amount
subto savings_deficit_calc:
    forall <m> in MONTHS:
        savings_deficit[m] >= DESIRED_SAVINGS - savings[m];

# Month-to-month stability for variable expenses
subto spending_stability:
    forall <c> in CATEGORIES:
        forall <m1, m2> in MONTHS * MONTHS with m2 == m1 + 1:
            forall <cat, minval, maxval, weight> in Bills with cat == c:
                if minval != maxval and weight > 0 then
                    spending[c,m1] - spending[c,m2] <= maxval * 0.2 and
                    spending[c,m2] - spending[c,m1] <= maxval * 0.2
                end;

minimize budget_allocation:
    (
        sum <m> in MONTHS: (
            sum <c> in CATEGORIES: (
                sum <cat, minval, maxval, weight> in Bills with cat == c and minval != maxval and weight > 0: (
                    (weight/10) * (spending[c,m] - maxval)^2
                )
            )
        )
    ) + (
        sum <m> in MONTHS: (
            (savings_deficit[m])^2
        )
    );
