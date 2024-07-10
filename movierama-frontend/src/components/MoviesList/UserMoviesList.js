import React, {useEffect, useState} from 'react';
import {Button, Modal} from 'react-bootstrap';
import api from '../../api/Api';
import AddMovieModal from '../Modals/NewMovie/AddMovieModal';
import SortingFilters from '../SortingFilters/SortingFilters';
import MovieCard from "./MovieCard";
import MoviePagination from "./MoviePagination";
import {checkUserVote, getDaysAgo} from '../../utils/Utils';
import {useLocation, useNavigate, useParams} from 'react-router-dom';


const UserMoviesList = ({user, userVotes, setUserVotes}) => {
    const [userMovies, setUserMovies] = useState([]);
    const [pageInfo, setPageInfo] = useState({});
    const [currentPage, setCurrentPage] = useState(1);
    const [sortBy, setSortBy] = useState('CREATED');
    const [showModal, setShowModal] = useState(false);
    const [showMovieModal, setShowMovieModal] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const location = useLocation();
    const navigate = useNavigate();
    const params = useParams();
    const userId = params.userId;
    const [fullName, setFullName] = useState('');
    const [sortDirection, setSortDirection] = useState('ASC');

    useEffect(() => {
        if (location.state?.fullName) {
            setFullName(location.state.fullName); // Set fullName from location state
        }
    }, [location.state]);

    const handleResetClick = () => {
        navigate('/');
    };

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const toggleMovieModal = () => {
        setShowMovieModal(!showMovieModal);
    };

    useEffect(() => {
        fetchUserData();
    }, [currentPage, sortBy, sortDirection]);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handleSortChange = (sortField) => {
        setSortBy(sortField);
    };

    const handleSortDirectionChange = (sortDirection) => {
        setSortDirection(sortDirection);
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
            const updatedMovies = userMovies.map(movie =>
                movie.id === movieId ? {
                    ...movie,
                    likesCount: response.data.likesCount,
                    hatesCount: response.data.hatesCount
                } : movie
            );
            setUserMovies(updatedMovies);
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

    const fetchUserData = async () => {
        try {
            const response = await api.get(`/api/v1/public/movies/owner/${userId}`, {
                params: {
                    sortField: sortBy.toUpperCase(),
                    page: currentPage - 1,
                    size: 10,
                    sortType: sortDirection
                },
                headers: {}
            });

            const updatedPageInfo = {
                ...response.data.page,
                totalPages: response.data.totalPages,
                totalElements: response.data.totalElements
            };
            setUserMovies(response.data.content);
            setPageInfo(updatedPageInfo);
        } catch (error) {
            console.error('Error fetching movies:', error);
        }
    };


    return (
        <>
            <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100">
                <div className="container mx-auto px-4 flex-grow">

                    <div className="container mx-auto px-4 py-3">
                        <div className="flex items-center mb-4">
                            <h1 className="text-2xl font-bold mr-4">You are viewing user {fullName}'s movies</h1>

                            <button
                                className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                                onClick={handleResetClick}
                            >
                                Reset
                            </button>
                        </div>
                    </div>


                    <SortingFilters sortByLikes={() => handleSortChange('LIKES')}
                                    sortByHates={() => handleSortChange('HATES')}
                                    sortByPublishedDate={() => handleSortChange('PUBLISHED')}
                                    sortByCreatedDate={() => handleSortChange('CREATED')}
                                    sortAsc={() => handleSortDirectionChange('ASC')}
                                    sortDesc={() => handleSortDirectionChange('DESC')}
                                    sortOrder={sortDirection}
                                    sortBy={sortBy}/>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div className="col-span-2">
                            {userMovies.length === 0 ? (
                                <p className="text-center text-gray-600 mt-8 mb-4 text-xl">
                                    <i className="far fa-film mr-2"></i>
                                    No movies found for this user.
                                </p>
                            ) : (
                                userMovies.map((movie) => (
                                    <MovieCard
                                        key={movie.id}
                                        movie={movie}
                                        fetchData={fetchUserData}
                                        getDaysAgo={getDaysAgo}
                                        handleVote={handleVote}
                                        checkUserVote={() => checkUserVote(userVotes, movie.id)}
                                    />
                                ))
                            )}
                        </div>
                        {user && (<div
                                className="col-span-1 flex justify-center items-center flex-col justify-self-center self-start">
                                <Button variant="danger" onClick={toggleMovieModal}>New Movie
                                </Button>
                            </div>
                        )}
                    < /div>
                    {userMovies && userMovies.length > 0 && (
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
                    fetchData={fetchUserData}
                />
            </div>
        </>
    );
};

export default UserMoviesList;
