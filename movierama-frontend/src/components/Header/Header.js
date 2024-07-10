import * as React from "react";
import {useState} from "react";
import {Button, Nav, Navbar} from 'react-bootstrap';
import LoginModal from "../Modals/Login/LoginModal";
import SignUpModal from "../Modals/SignUp/SignUpModal";
import api from "../../api/Api";
import {useNavigate} from "react-router-dom";


const Header = ({onLogin, user}) => {
    const navigate = useNavigate();

    const [showLoginModal, setShowLoginModal] = useState(false);
    const [showSignUpModal, setShowSignUpModal] = useState(false);

    const handleLoginClose = () => setShowLoginModal(false);
    const handleLoginShow = () => setShowLoginModal(true);

    const handleSignUpClose = () => setShowSignUpModal(false);
    const handleSignUpShow = () => setShowSignUpModal(true);

    const handleUserClick = (userId, fullName) => {
        navigate(`/user/${userId}/movies`, {state: {fullName}});
    };

    const handleLogout = () => {
        // Clear token from localStorage
        localStorage.removeItem('token');
        delete api.defaults.headers.common['Authorization'];
        // Reset user state
        onLogin(undefined, undefined);
    };

    return (
        <>
            <Navbar bg="dark" variant="dark" expand="lg">
                <Navbar.Brand href="/">MovieRama</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ml-auto">
                        {!user ? (
                            <>
                                <Nav.Item>
                                    <Button variant="outline-light" className="mr-2 md:mr-3" onClick={handleLoginShow}>Log
                                        In</Button>
                                </Nav.Item>
                                <Nav.Item>
                                    <Button variant="outline-success" className="mr-2 md:mr-3"
                                            onClick={handleSignUpShow}>Sign Up</Button>
                                </Nav.Item>
                            </>
                        ) : (
                            <div className="d-flex align-items-center">
                                <div className="text-light mr-2 md:mr-3">
                                    Welcome back <a
                                    href="#"
                                    onClick={() => handleUserClick(user.id, `${user.firstname} ${user.lastname}`)}
                                >
                                    {`${user.firstname} ${user.lastname}`}
                                </a>
                                </div>
                                <Button variant="outline-danger" size="sm" onClick={handleLogout}>Log Out</Button>
                            </div>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Navbar>

            <LoginModal show={showLoginModal} handleClose={handleLoginClose} onLogin={onLogin}/>
            <SignUpModal show={showSignUpModal} handleClose={handleSignUpClose}/>
        </>
    );
};

export default Header;
