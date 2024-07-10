import React, {useEffect, useState} from 'react';
import {Button, Modal} from 'react-bootstrap';
import api from '../../api/Api';
import AddMovieModal from '../Modals/NewMovie/AddMovieModal';
import SortingFilters from '../SortingFilters/SortingFilters';
import MovieCard from "./MovieCard";
import MoviePagination from "./MoviePagination";
import {checkUserVote, getDaysAgo} from '../../utils/Utils';
import EditMovieModal from "../Modals/EditMovieModal/EditMovieModal";


const MoviesList = ({user, userVotes, setUserVotes}) => {
    const [movies, setMovies] = useState([]);
    const [pageInfo, setPageInfo] = useState({});
    const [currentPage, setCurrentPage] = useState(1);
    const [sortBy, setSortBy] = useState('CREATED');
    const [showModal, setShowModal] = useState(false);
    const [showMovieModal, setShowMovieModal] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [sortDirection, setSortDirection] = useState('ASC');
    const [editMovieData, setEditMovieData] = useState({});
    const [showEditModal, setShowEditModal] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [fetchStatus, setFetchStatus] = useState('idle'); // idle, pending, succeeded, failed
    const [fetchError, setFetchError] = useState('');

    const handleEditClick = (movie) => {
        setEditMovieData(movie); // set movie data to edit
        setShowEditModal(true);
    };

    const handleCloseEditModal = () => {
        setShowEditModal(false);
        setEditMovieData({});
    };

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const toggleMovieModal = () => {
        setShowMovieModal(!showMovieModal);
    };

    useEffect(() => {
        fetchData();
    }, [currentPage, sortBy, sortDirection, searchQuery]);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handleSortChange = (sortField) => {
        setSortBy(sortField);
    };

    const handleSortDirectionChange = (sortDirection) => {
        setSortDirection(sortDirection);
    };

    const handleSearchChange = (e) => {
        setSearchQuery(e.target.value);
    };

    const clearSearch = () => {
        setSearchQuery('');
    };

    const handleEditMovie = async (movieId, updatedData) => {
        try {
            // Make the API call to update the movie
            await api.put(`/api/v1/movies/${movieId}`, updatedData, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            // Refresh the movie list
            fetchData();
            handleCloseEditModal();
        } catch (error) {
            console.error('Error editing movie:', error);
            // Handle error (e.g., show an error message)
        }
    };

    const handleVote = async (movieId, voteType) => {
        try {
            const voteResponse = await api.put(`/api/v1/votes/movie/${movieId}`, {
                voteType: voteType
            }, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            const response = await api.get(`/api/v1/movies/${movieId}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            const updatedMovies = movies.map(movie =>
                movie.id === movieId ? {
                    ...movie,
                    likesCount: response.data.likesCount,
                    hatesCount: response.data.hatesCount
                } : movie
            );
            setMovies(updatedMovies);
            if (voteResponse.type === null) {
                setUserVotes(prevUserVotes => prevUserVotes.filter(vote => vote.movieId !== movieId));
            } else {
                setUserVotes(prevUserVotes => {
                    // Filter out the existing vote for the movie
                    const updatedUserVotes = prevUserVotes.filter(vote => vote.movieId !== movieId);
                    // Add the new vote from voteResponse
                    return [...updatedUserVotes, voteResponse.data];
                });
            }
        } catch (error) {
            if (error.response.data.errorCode === 306) {
                setErrorMessage(error.response.data.errorDescription);
                toggleModal();
            }
        }
    };

    const fetchData = async () => {
        setFetchStatus('pending');
        try {
            const response = await api.get('/api/v1/public/movies', {
                params: {
                    sortField: sortBy.toUpperCase(),
                    page: currentPage - 1,
                    size: 10,
                    sortType: sortDirection,
                    title: searchQuery
                },
                headers: {}
            });

            const updatedPageInfo = {
                ...response.data.page,
                totalPages: response.data.totalPages,
                totalElements: response.data.totalElements
            };
            setMovies(response.data.content);
            setPageInfo(updatedPageInfo);
            setFetchStatus('succeeded');
        } catch (error) {
            console.error('Error fetching movies:', error);
            setFetchStatus('failed');
            setFetchError('Failed to fetch movies. Please try again later.');
        }
    };


    return (
        <>
            <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100">
                <div className="container mx-auto px-4 flex-grow">
                    <SortingFilters sortByLikes={() => handleSortChange('LIKES')}
                                    sortByHates={() => handleSortChange('HATES')}
                                    sortByPublishedDate={() => handleSortChange('PUBLISHED')}
                                    sortByCreatedDate={() => handleSortChange('CREATED')}
                                    sortAsc={() => handleSortDirectionChange('ASC')}
                                    sortDesc={() => handleSortDirectionChange('DESC')}
                                    sortOrder={sortDirection}
                                    sortBy={sortBy}/>
                    <div className="flex justify-center my-4">
                        <input
                            type="text"
                            placeholder="Search by title"
                            value={searchQuery}
                            onChange={handleSearchChange}
                            className="p-2 border rounded-md w-full md:w-1/2"
                        />
                        {searchQuery && (
                            <button
                                onClick={clearSearch}
                                className="ml-2 bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600"
                            >
                                Clear
                            </button>
                        )}
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div className="col-span-2">
                            {movies.map((movie) => (
                                <MovieCard
                                    key={movie.id}
                                    movie={movie}
                                    getDaysAgo={getDaysAgo}
                                    handleVote={handleVote}
                                    loggedInUserId={user ? user.id : null}
                                    checkUserVote={() => checkUserVote(userVotes, movie.id)}
                                    onEditClick={() => handleEditClick(movie)}

                                />
                            ))}
                        </div>
                        {user && (<div
                                className="col-span-1 flex justify-center items-center flex-col justify-self-center self-start">
                                <Button variant="danger" onClick={toggleMovieModal}>New Movie
                                </Button>
                            </div>
                        )}
                    < /div>

                    {movies && movies.length > 0 && (
                        <MoviePagination
                            currentPage={currentPage}
                            pageInfo={pageInfo}
                            handlePageChange={handlePageChange}
                        />)
                    }
                </div>

                <Modal show={showModal} onHide={toggleModal}>
                    <Modal.Header closeButton>
                        <Modal.Title>Error</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>{errorMessage}</Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={toggleModal}>
                            Close
                        </Button>
                    </Modal.Footer>
                </Modal>

                <AddMovieModal
                    showModal={showMovieModal}
                    toggleModal={toggleMovieModal}
                    fetchData={fetchData}
                />

                <EditMovieModal
                    showModal={showEditModal}
                    toggleModal={handleCloseEditModal}
                    movie={editMovieData}
                    onEditMovie={handleEditMovie}
                />

                <div className="absolute bottom-4 right-4">
                    {fetchStatus === 'failed' && (
                        <div className="bg-red-500 text-white p-4 rounded flex items-center justify-between">
                            <span className="mr-4">{fetchError}</span> {/* Added mr-4 for right margin */}
                            <button onClick={fetchData}
                                    className="bg-white text-red-500 px-4 py-2 rounded-md hover:bg-red-100">
                                Retry
                            </button>
                        </div>
                    )}
                </div>


            </div>
        </>
    );
};

export default MoviesList;
