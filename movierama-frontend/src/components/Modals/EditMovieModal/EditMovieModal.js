import React, {useEffect, useState} from 'react';
import {Modal, Button, Form} from 'react-bootstrap';

const EditMovieModal = ({showModal, toggleModal, movie, onEditMovie}) => {
    const [editedMovie, setEditedMovie] = useState(movie || {});

    useEffect(() => {
        setEditedMovie(movie);
    }, [movie]);

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setEditedMovie({...editedMovie, [name]: value});
    };

    const handleSubmit = () => {
        onEditMovie(editedMovie.id, editedMovie);
    };

    return (
        <Modal show={showModal} onHide={toggleModal}>
            <Modal.Header closeButton>
                <Modal.Title>Edit Movie</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group controlId="title">
                        <Form.Label>Title</Form.Label>
                        <Form.Control
                            type="text"
                            name="title"
                            value={editedMovie.title || ''}
                            onChange={handleInputChange}
                        />
                    </Form.Group>
                    <Form.Group controlId="description">
                        <Form.Label>Description</Form.Label>
                        <Form.Control
                            as="textarea"
                            name="description"
                            value={editedMovie.description || ''}
                            onChange={handleInputChange}
                        />
                    </Form.Group>
                    <Form.Group controlId="publicationDate">
                        <Form.Label>Publication Date</Form.Label>
                        <Form.Control
                            type="date"
                            name="publicationDate"
                            value={editedMovie.publicationDate || ''}
                            onChange={handleInputChange}
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={() => {
                }}>
                    Close
                </Button>
                <Button variant="primary" onClick={handleSubmit}>
                    Save Changes
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default EditMovieModal;
