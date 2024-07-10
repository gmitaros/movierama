export const getDaysAgo = (date) => {
    const currentDate = new Date();
    const postDate = new Date(date);
    const diffTime = Math.abs(currentDate - postDate);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return `${diffDays} days ago`;
};

export const checkUserVote = (userVotes, movieId) => {
    const userVote = userVotes.find(vote => vote.movieId === movieId);
    return userVote ? userVote.type : null;
};