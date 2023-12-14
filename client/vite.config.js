import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import svgr from 'vite-plugin-svgr';
import viteTsConfigPaths from 'vite-tsconfig-paths';

export default defineConfig(() => {
	return {
		server: {
			open: true,
		},
		build: {
			outDir: 'build',
		},
		plugins: [
			react(),
			// svgr options: https://react-svgr.com/docs/options/
			svgr({ svgrOptions: { icon: true } }),
			viteTsConfigPaths()
		],
	};
});
