import tailwindcss from "tailwindcssl";
module.exports = {
  plugins: [tailwindcss("./tailwind.js"), require("autoprefixer")],
};
