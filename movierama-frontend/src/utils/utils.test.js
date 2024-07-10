import {getDaysAgo, checkUserVote} from './Utils'; // Replace with your actual utility file name

const subtractDaysFromDate = (days) => {
    const currentDate = new Date();
    return new Date(currentDate.setDate(currentDate.getDate() - days));
};

describe('getDaysAgo', () => {
    it('returns the correct number of days ago', () => {
        const result = getDaysAgo(subtractDaysFromDate(3));
        expect(result).toBe('3 days ago');
    });

    it('handles 5 days ago', () => {
        const result = getDaysAgo(subtractDaysFromDate(5));
        expect(result).toBe('5 days ago');
    });

    it('handles same day', () => {
        const result = getDaysAgo(new Date());
        expect(result).toBe('0 days ago');
    });
});

describe('checkUserVote', () => {
    const userVotes = [
        {movieId: 1, type: 'LIKE'},
        {movieId: 2, type: 'HATE'},
        {movieId: 3, type: 'LIKE'},
    ];

    it('returns vote type for existing movieId', () => {
        const result = checkUserVote(userVotes, 1);
        expect(result).toBe('LIKE');
    });

    it('returns null for non-existing movieId', () => {
        const result = checkUserVote(userVotes, 4);
        expect(result).toBeNull();
    });
});

