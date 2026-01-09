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