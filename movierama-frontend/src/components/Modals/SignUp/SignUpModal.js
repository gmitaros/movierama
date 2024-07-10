import React, {useState} from 'react';
import {Button, Form, Modal} from 'react-bootstrap';
import api from '../../../api/Api';
import ConfirmationModal from "./ConfirmationModal";

const SignUpModal = ({show, handleClose}) => {

    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [errors, setErrors] = useState({});
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [confirmationMessage, setConfirmationMessage] = useState('');

    const handleFirstnameChange = (e) => setFirstname(e.target.value);
    const handleLastnameChange = (e) => setLastname(e.target.value);
    const handleEmailChange = (e) => setEmail(e.target.value);
    const handlePasswordChange = (e) => setPassword(e.target.value);

    const handleShowConfirmation = () => {
        setShowConfirmation(true);
        setConfirmationMessage('Registration completed. Please check your account for activation link.');
    };

    const handleCloseConfirmation = () => {
        setShowConfirmation(false);
        setConfirmationMessage('');
        handleClose();
    };

    const validateEmail = () => {
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailPattern.test(email)) {
            setErrors(prevErrors => ({...prevErrors, email: 'Please enter a valid email address'}));
            return false;
        } else {
            setErrors(prevErrors => ({...prevErrors, email: ''}));
            return true;
        }
    };

    const validatePassword = () => {
        if (password && password.length < 8) {
            setErrors(prevErrors => ({...prevErrors, password: 'Password should be 8 characters long minimum'}));
            return false;
        } else if (password === undefined || password === '') {
            setErrors(prevErrors => ({...prevErrors, password: 'Password should be 8 characters long minimum'}));
            return false;
        } else {
            setErrors(prevErrors => ({...prevErrors, password: ''}));
            return true;
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const isEmailValid = validateEmail();
        const isPasswordValid = validatePassword();

        if (!isEmailValid || !isPasswordValid) {
            return; // Don't proceed if validation fails
        }

        try {
            await api.post('/api/v1/auth/register', {
                firstname,
                lastname,
                email,
                password
            });
            handleShowConfirmation(); // Show confirmation modal on successful registration
            handleClose();
        } catch (err) {
            setError(err.response ? err.response.data.errorDescription : 'An error occurred');
        }
    };

    return (
        <>
            <Modal show={show} onHide={handleClose}>
                <Form onSubmit={handleSubmit}>
                    <Modal.Header closeButton>
                        <Modal.Title>Sign Up</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form.Group controlId="formFirstname">
                            <Form.Label>First Name</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Enter first name"
                                value={firstname}
                                onChange={handleFirstnameChange}
                                required
                            />
                            {errors.firstname && <small className="text-danger">{errors.firstname}</small>}
                        </Form.Group>
                        <Form.Group controlId="formLastname">
                            <Form.Label>Last Name</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Enter last name"
                                value={lastname}
                                onChange={handleLastnameChange}
                                required
                            />
                            {errors.lastname && <small className="text-danger">{errors.lastname}</small>}
                        </Form.Group>
                        <Form.Group controlId="formEmail">
                            <Form.Label>Email address</Form.Label>
                            <Form.Control
                                type="email"
                                placeholder="Enter email"
                                value={email}
                                onChange={handleEmailChange}
                                onBlur={validateEmail}
                                required
                            />
                            {errors.email && <small className="text-danger">{errors.email}</small>}
                        </Form.Group>

                        <Form.Group controlId="formPassword">
                            <Form.Label>Password</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Password"
                                value={password}
                                onChange={handlePasswordChange}
                                onBlur={validatePassword}
                                required
                            />
                            {errors.password && <small className="text-danger">{errors.password}</small>}
                        </Form.Group>

                    </Modal.Body>
                    {error && <p className="text-danger text-center w-full mb-4">{error}</p>}
                    <Modal.Footer>
                        <Modal.Footer>
                            <Button variant="primary" type="submit">
                                Sign Up
                            </Button>
                            <Button variant="secondary" onClick={handleClose}>
                                Close
                            </Button>
                        </Modal.Footer>
                    </Modal.Footer>
                </Form>

            </Modal>
            <ConfirmationModal
                show={showConfirmation}
                handleClose={handleCloseConfirmation}
                message={confirmationMessage}
            />
        </>
    );
};

export default SignUpModal;
