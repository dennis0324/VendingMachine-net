/**
 * @module types
 */
/**
 * @typedef ipcDto
 * @type {Object}
 *
 * @property {string} hash - the hahs value of cmd
 * @property {string} cmd - the cmd that operate
 * @property {string} vendingId - the vending machine id
 * @property {string} date - the date of the operation
 * @property {Object} payload - the data to be sent
 */

/**
 * @typedef ProductDto
 * @type {Object}
 *
 * @property {string} productId - the name of the product
 * @property {string} price - the price of the product
 * @property {int} qty - the quantity of the product
 * @property {string} name - the name of the product
 * @property {string} price - the price of the product
 */

/**
 * @typedef ReturnPayload
 * @type {Object}
 *
 * @property {string} status - the status of the operation
 * @property {JSON | JSON[]} data - the message of the operation
 */

/**
 * @typedef MoneyDto
 * @type {Object}
 *
 * @property {int} use - the money to be used
 * @property {int} price - the price of the product
 */

// @ts-ignore
module.exports = {};
