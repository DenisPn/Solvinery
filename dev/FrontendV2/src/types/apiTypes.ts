export interface ModelPayload {
  variables: {
    identifier: string;
    structure: string[];
    alias: string;
    objectiveValueAlias: string;
  }[];
  constraintModules: {
    moduleName: string;
    description: string;
    constraints: string[];
    active: boolean;
  }[];
  preferenceModules: {
    moduleName: string;
    description: string;
    preferences: string[];
    scalar: number;
  }[];
  sets: {
    setDefinition: {
      name: string;
      structure: string[];
      alias: string;
    };
    values: string[];
  }[];
  parameters: {
    parameterDefinition: {
      name: string;
      structure: string;
      alias: string;
    };
    value: string;
  }[];
  name: string;
  description: string;
  code: string;
}

export interface ImageDto {
  name: string;
  description: string;
  creationDate: string;
  authorName: string;
}

export interface PaginatedImagesResponse {
  images: Record<string, ImageDto>; // מפה שבה המפתח הוא ה-ID
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface ImageSearchParams {
  page?: number;
  size?: number;
  name?: string;
  description?: string;
  author?: string;
}