import React from 'react';
import {Button, Card} from 'react-bootstrap';
import {useNavigate} from "react-router-dom";
import {FaEdit} from 'react-icons/fa';

const MovieCard = ({movie, getDaysAgo, handleVote, loggedInUserId, checkUserVote, onEditClick}) => {
    const navigate = useNavigate();

    const handleUserClick = (userId, fullName) => {
        navigate(`/user/${userId}/movies`, {state: {fullName}});
    };

    return (
        <div key={movie.id} className="mb-4">
            <Card className="w-full">{movie.user.id === loggedInUserId && (
                <Button
                    variant="outline-secondary"
                    className="position-absolute top-0 end-0 m-2"
                    onClick={() => onEditClick(movie)}
                >
                    <FaEdit/>
                </Button>
            )}

                <Card.Body>
                    <Card.Title>{movie.title}</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        Posted by{' '}
                        <a
                            href="#"
                            onClick={() => handleUserClick(movie.user.id, `${movie.user.firstname} ${movie.user.lastname}`)}
                        >
                            {movie.user.firstname} {movie.user.lastname}
                        </a>{' '}
                        {getDaysAgo(movie.createdDate)}
                        <br/>
                        Publication Date: {new Date(movie.publicationDate).toLocaleDateString()}
                    </Card.Subtitle>
                    <Card.Text>{movie.description}</Card.Text>
                    <div className="d-flex justify-content-between align-items-center">
                        <Card.Text>
                            <Button variant="outline-primary"
                                    onClick={() => handleVote(movie.id, 'LIKE')}
                                    disabled={!localStorage.getItem('token')}>Like
                                ({movie.likesCount})</Button>{' '}
                            <Button variant="outline-danger"
                                    onClick={() => handleVote(movie.id, 'HATE')}
                                    disabled={!localStorage.getItem('token')}>Hate
                                ({movie.hatesCount})</Button>
                        </Card.Text>
                        <Card.Text className="text-right">
                            {checkUserVote(movie.id) === 'LIKE' && <>
                                <span>You like this movie</span> |
                                <Button variant="link" size="sm"
                                        onClick={() => handleVote(movie.id, 'LIKE')}
                                >
                                    Unlike
                                </Button>
                            </>}
                            {checkUserVote(movie.id) === 'HATE' && <>
                                <span>You hate this movie</span> |
                                <Button variant="link" size="sm"
                                        onClick={() => handleVote(movie.id, 'HATE')}
                                >
                                    Unhate
                                </Button>
                            </>}
                        </Card.Text>
                    </div>
                </Card.Body>
            </Card>
        </div>
    );
}

export default MovieCard;
