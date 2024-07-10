import React, {useState} from 'react';
import {Button, Form, Modal} from 'react-bootstrap';
import api from '../../../api/Api';

const AddMovieModal = ({showModal, toggleModal, fetchData}) => {
    const [newMovie, setNewMovie] = useState({
        title: '',
        description: '',
        publicationDate: ''
    });
    const [confirmationModal, setConfirmationModal] = useState(false);
    const [validated, setValidated] = useState(false);


    const handleAddMovie = async (event) => {
        event.preventDefault(); // Prevent default form submission behavior

        const form = event.currentTarget;

        if (form.checkValidity() === false) {
            setValidated(true);
            return;
        }

        try {
            await api.post('/api/v1/movies', newMovie, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'application/json'
                }
            });
            setConfirmationModal(true);
            toggleModal(); // Close the input modal
            fetchData(); // Fetch updated movie list
            setNewMovie({
                title: '',
                description: '',
                publicationDate: ''
            });
            setValidated(false);
        } catch (error) {
            console.error('Error adding movie:', error);
        }
    };

    return (
        <>
            <Modal show={showModal} onHide={toggleModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Add Movie</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form noValidate validated={validated} onSubmit={handleAddMovie}>
                        <Form.Group className="mb-3" controlId="title">
                            <Form.Label>Title</Form.Label>
                            <Form.Control
                                required
                                type="text"
                                value={newMovie.title}
                                onChange={(e) => setNewMovie({...newMovie, title: e.target.value})}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="description">
                            <Form.Label>Description</Form.Label>
                            <Form.Control
                                required
                                as="textarea"
                                rows={3}
                                value={newMovie.description}
                                onChange={(e) => setNewMovie({...newMovie, description: e.target.value})}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="publicationDate">
                            <Form.Label>Publication Date</Form.Label>
                            <Form.Control
                                required
                                type="date"
                                value={newMovie.publicationDate}
                                onChange={(e) => setNewMovie({...newMovie, publicationDate: e.target.value})}
                            />
                        </Form.Group>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={toggleModal}>
                                Close
                            </Button>
                            <Button type="submit" variant="primary">
                                Add Movie
                            </Button>
                        </Modal.Footer>
                    </Form>
                </Modal.Body>
            </Modal>

            <Modal show={confirmationModal} onHide={() => setConfirmationModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Success</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Movie added successfully!
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setConfirmationModal(false)}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>

        </>
    );
};

export default AddMovieModal;
