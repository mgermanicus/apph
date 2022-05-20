export const openFullScreenById = (id: string) => {
  setTimeout(() => {
    const element = document.getElementById(id);
    if (element?.requestFullscreen) {
      element.requestFullscreen();
    }
  }, 100);
};
