/**
 * Sleep for a given amount of time.
 *
 * @params {number} ms - The number of milliseconds to sleep for.
 */
export const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
