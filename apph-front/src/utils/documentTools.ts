export const openFullScreenById = (id: string) => {
  setTimeout(async () => {
    const element = document.getElementById(id);
    if (element?.requestFullscreen) {
      await element.requestFullscreen();
    }
  }, 100);
};
