

function equalSplit(amount, participantIds, paidBy) {
    const totalCents = Math.round(amount * 100);
    const n = participantIds.length;
    const baseCents = Math.floor(totalCents / n);
    const remainderCents = totalCents - baseCents * n;

    return participantIds.map((userId) => ({
        user_id: userId,
        share_amount: (baseCents + (userId === paidBy ? remainderCents : 0)) / 100,
    }));
}

function percentageSplit(amount, percentageSplits, paidBy) {
    const totalCents = Math.round(amount * 100);
    let assignedCents = 0;

    const shares = percentageSplits.map(({ user_id, percentage }) => {
        const cents = Math.round((totalCents * percentage) / 100);
        assignedCents += cents;
        return { user_id, cents };
    });

    const remainderCents = totalCents - assignedCents;
    return shares.map(({ user_id, cents }) => ({
        user_id,
        share_amount: (cents + (user_id === paidBy ? remainderCents : 0)) / 100,
    }));
}

function exactSplit(amount, exactSplits) {
    const totalCents = Math.round(amount * 100);
    const sumCents = exactSplits.reduce((sum, s) => sum + Math.round(s.share_amount * 100), 0);

    if (sumCents !== totalCents) {
        throw new Error(`Exact splits sum to ${sumCents / 100}, but amount is ${amount}`);
    }
    return exactSplits.map((s) => ({ user_id: s.user_id, share_amount: s.share_amount }));
}

/**
 * items: [{ item_name, item_price, assigned_to_user_id }]
 * Returns both the computed splits AND the true total (subtotal + tax),
 * since for itemized expenses the "amount" isn't known until the items
 * are summed — the controller uses the returned total, not a client-sent one.
 */
function itemizedSplit(items, taxAmount, paidBy) {
    const subtotalByUser = {};
    let subtotalCents = 0;

    for (const item of items) {
        const cents = Math.round(item.item_price * 100);
        subtotalByUser[item.assigned_to_user_id] = (subtotalByUser[item.assigned_to_user_id] || 0) + cents;
        subtotalCents += cents;
    }

    const participantIds = Object.keys(subtotalByUser);
    const taxCents = Math.round(taxAmount * 100);
    const n = participantIds.length;
    const baseTaxCents = Math.floor(taxCents / n);
    const remainderTaxCents = taxCents - baseTaxCents * n;

    const splits = participantIds.map((userId) => {
        const ownTax = baseTaxCents + (userId === paidBy ? remainderTaxCents : 0);
        return { user_id: userId, share_amount: (subtotalByUser[userId] + ownTax) / 100 };
    });

    return { splits, totalAmount: (subtotalCents + taxCents) / 100 };
}

module.exports = { equalSplit, percentageSplit, exactSplit, itemizedSplit };