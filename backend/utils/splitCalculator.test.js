
const { equalSplit, exactSplit } = require('./splitCalculator');

describe('equalSplit', () => {
  it('splits evenly when the amount divides cleanly', () => {
    const result = equalSplit(30, ['u1', 'u2', 'u3'], 'u1');
    expect(result).toEqual([
      { user_id: 'u1', share_amount: 10 },
      { user_id: 'u2', share_amount: 10 },
      { user_id: 'u3', share_amount: 10 },
    ]);
  });

  it('gives the rounding remainder to whoever paid, not split evenly', () => {
    // $10 / 3 people = 333.33... cents each — someone has to eat the extra cent
    const result = equalSplit(10, ['u1', 'u2', 'u3'], 'u2');
    const byUser = Object.fromEntries(result.map(s => [s.user_id, s.share_amount]));
    expect(byUser.u2).toBeCloseTo(3.34); // payer u2 absorbs the odd cent
    expect(byUser.u1).toBeCloseTo(3.33);
    expect(byUser.u3).toBeCloseTo(3.33);
  });

  it('every split still sums to the original amount, to the cent', () => {
    const result = equalSplit(10, ['u1', 'u2', 'u3'], 'u1');
    const sum = result.reduce((s, r) => s + r.share_amount, 0);
    expect(sum).toBeCloseTo(10);
  });
});

describe('exactSplit', () => {
  it('throws when the splits do not sum to the amount', () => {
    expect(() =>
      exactSplit(50, [{ user_id: 'u1', share_amount: 20 }, { user_id: 'u2', share_amount: 20 }])
    ).toThrow(/sum to 40/);
  });
});