# Movierama Frontend

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Local Development

### Prerequisites

- Node.js installed on your machine

```shell
PS E:\my-projects\movierama> node -v
v20.12.2
PS E:\my-projects\movierama> npm -v
10.5.0
```

### Running Locally

1. Clone the repository:

    ```bash
    git clone https://github.com/gmitaros/movierama.git
    ```

2. Navigate to the project directory:

    ```bash
    cd movierama/movierama-frontend
    ```

3. Install dependencies:

    ```bash
    npm install
    ```

4. Create a `.env` file in the root directory and add the following environment variables:
   #### Localhost
   ```
   REACT_APP_HOST=http://localhost:8088
    ```
   #### Staging 
   ```
    REACT_APP_HOST=https://movierama.aboveaverage.dev
   ```

5. Start the development server:

    ```bash
    npm start
    ```

6. Open [http://localhost:3000](http://localhost:3000) in your browser to view the application.

The page will reload when you make changes.\
You may also see any lint errors in the console.

## MoviesList Component

The `MoviesList` component serves as the main view for displaying a list of movies, sorting options, and a search
feature.

### Features:

- **Sorting Filters**
    - Sorting movies by likes, hates, publication date, and added date.
    - Sorting movies in ascending or descending order.

- **Search by Title**
    - A search input field allows users to search movies by title.
    - A "Clear" button to reset the search query and fetch all movies again.

- **Pagination**
    - Pagination controls to navigate through different pages of movie results.

- **Vote Handling**
    - Users can vote for movies by liking or hating them.
    - Displaying likes and hates count for each movie.
    - Updating user votes based on the vote response.

- **Error Handling**
    - Displaying error messages in a modal when an API call fails.

- **New Movie Modal**
    - A modal to add a new movie to the list.

## SortingFilters Component

The `SortingFilters` component provides sorting options for movies.

### Features:

- **Sort by Category**
    - Sorting movies by likes, hates, publication date, and added date.

- **Sort Order**
    - Choosing between ascending and descending order.

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes.\
You may also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more
information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### Deployment

This section has moved
here: [https://facebook.github.io/create-react-app/docs/deployment](https://facebook.github.io/create-react-app/docs/deployment)
