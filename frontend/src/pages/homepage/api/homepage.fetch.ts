import { schemaHomepage } from '../model/homepage.types';
import { homepageMock } from './homepage.mock';

export const homepageApi = {
  async get() {
    return schemaHomepage.parse(homepageMock);
  },
};
