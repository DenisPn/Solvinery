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

// ✅ השינוי החשוב: ImageDto יורש עכשיו את כל השדות של ModelPayload
export interface ImageDto extends ModelPayload {
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

export interface SolverConfig {
  preferenceModulesScalars: Record<string, number>;
  enabledConstraintModules: string[];
  timeout: number;
}

export interface SolverSolutionValue {
  values: string[];
  objectiveValue: number;
}

export interface SolverResponse {
  solved: boolean;
  solvingTime: number;
  objectiveValue: number;
  solution: Record<string, any>; 
  [key: string]: any; 
}