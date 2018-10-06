from setuptools import setup, find_packages

setup(
    name='accountService',
    packages=find_packages(),
    include_package_data=True,
    install_requires=[
        'flask',
        'Flask-API',
        'Flask-SQLAlchemy',
        'SQLAlchemy',
        'requests',
    ],
    setup_requires=[
        'pytest-runner'
    ],
    tests_require=[
        'pytest'
    ],
)
