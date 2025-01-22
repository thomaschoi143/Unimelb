# COMP90086 Computer Vision Project
- Thomas Choi (1202247)
- Angela Yifei Yuan (1269549)


## Expected Structure
```bash
├── COMP90086_2024_Project_test
│   ├── prediction_resnet.csv
├── COMP90086_2024_Project_train
├── data_exploration_augmentation.ipynb
├── iterative_stability_pred_pipeline.ipynb
├── direct_stability_pred_pipeline.ipynb
└── README.md
```
- [__COMP90086_2024_Project_test__](./COMP90086_2024_Project_test/): unzipped COMP90086_2024_Project_test.zip downloaded from LMS
- [__COMP90086_2024_Project_train__](./COMP90086_2024_Project_train/): unzipped COMP90086_2024_Project_train.zip downloaded from LMS
- [__data_exploration_augmentation.ipynb__](./data_exploration_augmentation.ipynb): explore dataset, and generate offline augmented images
- [__iterative_stability_pred_pipeline.ipynb__](./iterative_stability_pred_pipeline.ipynb): implement the proposed Iterative Stability Prediction Pipeline, model training and evaluation
- [__direct_stability_pred_pipeline.ipynb__](./direct_stability_pred_pipeline.ipynb): implement the baseline Direct Stability Prediction Pipeline, model training and prediction
- [__prediction_resnet.csv__](./COMP90086_2024_Project_test/prediction_resnet.csv): final test predictions in Kaggle format


## Result Recreation
1. generate background removed and offline augmented training and testing samples using [__data_exploration_augmentation.ipynb__](./data_exploration_augmentation.ipynb)
2. run [__iterative_stability_pred_pipeline.ipynb__](./iterative_stability_pred_pipeline.ipynb) to train a model of __proposed__ method and generate test prediction
3. run [__direct_stability_pred_pipeline.ipynb__](./direct_stability_pred_pipeline.ipynb) to train a model of __baseline__ method and generate test prediction