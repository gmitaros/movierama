import React from "react";
import {Accordion, Button, ButtonGroup, ButtonToolbar} from "react-bootstrap";
import * as PropTypes from "prop-types";

class SortingFilters extends React.Component {
    render() {
        return <Accordion defaultActiveKey={["0"]} alwaysOpen className="my-4">
            <Accordion.Item eventKey="0">
                <Accordion.Header>Sorting filters</Accordion.Header>
                <Accordion.Body>
                    <ButtonToolbar aria-label="Toolbar with button groups">

                        <ButtonGroup className="me-2" aria-label="Sorting filters">
                            <Button onClick={this.props.sortByLikes}
                                    variant={this.props.sortBy === "LIKES" ? "primary" : "secondary"}>Likes</Button>
                            <Button onClick={this.props.sortByHates}
                                    variant={this.props.sortBy === "HATES" ? "primary" : "secondary"}>Hates</Button>
                            <Button onClick={this.props.sortByPublishedDate}
                                    variant={this.props.sortBy === "PUBLISHED" ? "primary" : "secondary"}>Publication
                                Date</Button>
                            <Button onClick={this.props.sortByCreatedDate}
                                    variant={this.props.sortBy === "CREATED" ? "primary" : "secondary"}>Added
                                Date</Button>
                        </ButtonGroup>
                        <ButtonGroup className="me-2" aria-label="Sorting filters">
                            <Button onClick={this.props.sortAsc}
                                    variant={this.props.sortOrder === "ASC" ? "primary" : "secondary"}>Asc</Button>
                            <Button onClick={this.props.sortDesc}
                                    variant={this.props.sortOrder === "DESC" ? "primary" : "secondary"}>Desc</Button>
                        </ButtonGroup>
                    </ButtonToolbar>

                </Accordion.Body>
            </Accordion.Item>
        </Accordion>;
    }
}

SortingFilters.propTypes = {
    sortByLikes: PropTypes.func,
    sortBy: PropTypes.string,
    sortByHates: PropTypes.func,
    sortByPublishedDate: PropTypes.func,
    sortByCreatedDate: PropTypes.func,
    sortAsc: PropTypes.func,
    sortDesc: PropTypes.func,
    sortOrder: PropTypes.string
};

export default SortingFilters;
