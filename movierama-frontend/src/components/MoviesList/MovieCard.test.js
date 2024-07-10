import React from 'react';
import {render, screen, fireEvent} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import MovieCard from './MovieCard';

describe('MovieCard', () => {
    const currentDate = new Date();
    currentDate.setDate(currentDate.getDate() - 2); // Subtract 2 days
    const movie = {
        id: 1,
        title: 'Test Movie',
        user: {
            id: 1,
            firstname: 'John',
            lastname: 'Doe'
        },
        createdDate: currentDate.toISOString(),
        description: 'Test description',
        likesCount: 5,
        hatesCount: 2
    };

    const fetchData = jest.fn();
    const getDaysAgo = jest.fn();
    const handleVote = jest.fn();
    const checkUserVote = jest.fn();

    it('renders movie card correctly', () => {
        render(
            <MovieCard
                movie={movie}
                fetchData={fetchData}
                getDaysAgo={getDaysAgo}
                handleVote={handleVote}
                checkUserVote={checkUserVote}
            />
        );

        expect(screen.getByText('Test Movie')).toBeInTheDocument();
        expect(screen.getByText('Posted by')).toBeInTheDocument();
        expect(screen.getByText('John Doe')).toBeInTheDocument();
        expect(screen.getByText('Test description')).toBeInTheDocument();
    });

    it('calls handleVote when Like button is clicked', () => {
        render(
            <MovieCard
                movie={movie}
                fetchData={fetchData}
                getDaysAgo={getDaysAgo}
                handleVote={handleVote}
                checkUserVote={checkUserVote}
            />
        );

        fireEvent.click(screen.getByText('Like (5)'));
        expect(handleVote).toHaveBeenCalledWith(1, 'LIKE');
    });

    it('calls handleVote when Hate button is clicked', () => {
        render(
            <MovieCard
                movie={movie}
                fetchData={fetchData}
                getDaysAgo={getDaysAgo}
                handleVote={handleVote}
                checkUserVote={checkUserVote}
            />
        );

        fireEvent.click(screen.getByText('Hate (2)'));
        expect(handleVote).toHaveBeenCalledWith(1, 'HATE');
    });
});
